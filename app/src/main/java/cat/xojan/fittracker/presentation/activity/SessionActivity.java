package cat.xojan.fittracker.presentation.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.result.SessionReadResult;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import cat.xojan.fittracker.BuildConfig;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.presentation.BaseActivity;
import cat.xojan.fittracker.presentation.listener.UiContentUpdater;
import cat.xojan.fittracker.presentation.presenter.SessionPresenter;
import cat.xojan.fittracker.presentation.presenter.UnitDataPresenter;
import cat.xojan.fittracker.util.SessionDetailedData;
import cat.xojan.fittracker.util.SessionMapData;
import cat.xojan.fittracker.util.Utils;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SessionActivity extends BaseActivity
        implements UiContentUpdater{

    private static final String TAG = SessionActivity.class.getSimpleName();

    @Bind(R.id.fragment_session_toolbar) Toolbar toolbar;

    @Inject
    UnitDataPresenter mUnitDataPresenter;
    @Inject
    SessionPresenter mSessionPresenter;

    private Session mSession;
    private MenuItem mDeleteButton;
    private List<DataSet> mDataSets;
    private List<DataPoint> mLocationDataPoints;
    private List<DataPoint> mSegmentDataPoints;
    private GoogleMap map;
    private List<DataPoint> mDistanceDataPoints;
    private int mActiveTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        mSession = Session.extract(intent);
        showProgress();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_main, menu);

        mDeleteButton = menu.findItem(R.id.action_delete);
        mDeleteButton.setVisible(false);
        MenuItem music = menu.findItem(R.id.action_music);
        music.setVisible(false);
        MenuItem settings = menu.findItem(R.id.action_settings);
        settings.setVisible(false);
        MenuItem attributions = menu.findItem(R.id.action_attributions);
        attributions.setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.delete_session)
                        .setPositiveButton(R.string.delete, (dialog, id1) -> {
                            mSessionPresenter.deleteSession(mSession, null /*getGoogleApiClient()*/, this);
                        })
                        .setNegativeButton(R.string.cancel, (dialog, id1) -> {
                            // User cancelled the dialog
                        });
                // Create the AlertDialog object and return it
                builder.create().show();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fillViewContent() {
        getDataPoints();

        Observable.just(this)
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Subscriber<SessionActivity>() {

                    private LinearLayout intervalView;
                    private double totalDistance;

                    @Override
                    public void onCompleted() {
                        Observable.just("")
                                .subscribeOn(Schedulers.newThread())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Subscriber<String>() {
                                    @Override
                                    public void onCompleted() {}

                                    @Override
                                    public void onError(Throwable e) {
                                        Log.e(TAG, "Error showing detailed data");
                                    }

                                    @Override
                                    public void onNext(String s) {
                                        showDetailedData(intervalView, totalDistance);
                                    }
                                });
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Error getting Datapoints: set detailed data");
                    }

                    @Override
                    public void onNext(SessionActivity sessionActivity) {
                        SessionDetailedData detailedData = new SessionDetailedData(sessionActivity,
                                mUnitDataPresenter);
                        detailedData.readDetailedData(mLocationDataPoints, mSegmentDataPoints);
                        intervalView = detailedData.getIntervalView();
                        totalDistance = detailedData.getTotalDistance();
                    }
                });
    }

    private void showDetailedData(LinearLayout intervalView, double totalDistance) {
        if (intervalView != null) {
            LinearLayout detailedView = (LinearLayout) findViewById(R.id.session_intervals);
            detailedView.removeAllViews();
            detailedView.addView(intervalView);
        } else {
            totalDistance = 0;
            for (DataPoint dp : mDistanceDataPoints) {
                totalDistance = totalDistance + dp.getValue(Field.FIELD_DISTANCE).asFloat();
            }
        }
        //distance
        ((TextView) findViewById(R.id.fragment_session_total_distance))
                .setText(Utils.getRightDistance((float) totalDistance, SessionActivity.this));
        //name
        ((TextView) findViewById(R.id.fragment_session_name))
                .setText(mSession.getName());
        //description
        ((TextView) findViewById(R.id.fragment_session_description))
                .setText(mSession.getDescription());
        //date
        ((TextView) findViewById(R.id.fragment_session_date))
                .setText(Utils.getRightDate(mSession.getStartTime(TimeUnit.MILLISECONDS), SessionActivity.this));
        //start time
        ((TextView) findViewById(R.id.fragment_session_start))
                .setText(Utils.millisToTime(mSession.getStartTime(TimeUnit.MILLISECONDS)));
        //end time
        ((TextView) findViewById(R.id.fragment_session_end))
                .setText(Utils.millisToTime(mSession.getEndTime(TimeUnit.MILLISECONDS)));
        //total/duration time
        ((TextView) findViewById(R.id.fragment_session_total_time))
                .setText(Utils.getTimeDifference(mSession.getEndTime(TimeUnit.MILLISECONDS),
                        mSession.getStartTime(TimeUnit.MILLISECONDS)));

        long sessionTime = mSession.getEndTime(TimeUnit.MILLISECONDS) - mSession.getStartTime(TimeUnit.MILLISECONDS);
        sessionTime = mActiveTime > 0 ? mActiveTime : sessionTime;

        if (totalDistance > 0 && sessionTime > 0) {
            double speed = totalDistance / (sessionTime / 1000);
            //speed
            ((TextView) findViewById(R.id.fragment_session_total_speed)).setText(Utils.getRightSpeed((float) speed, SessionActivity.this));
            //pace
            ((TextView) findViewById(R.id.fragment_session_total_pace)).setText(Utils.getRightPace((float) speed, SessionActivity.this));
        } else {
            //speed
            ((TextView) findViewById(R.id.fragment_session_total_speed)).setText(Utils.getRightSpeed(0, SessionActivity.this));
            //pace
            ((TextView) findViewById(R.id.fragment_session_total_pace)).setText(Utils.getRightPace(0, SessionActivity.this));
        }
        dismissProgress();
        if (mLocationDataPoints != null && mLocationDataPoints.size() > 0) {
            fillMap(true);
        } else {
            fillMap(false);
        }
    }

    private void getDataPoints() {
        mActiveTime = 0;
        mDistanceDataPoints = null;
        mLocationDataPoints = null;
        mSegmentDataPoints = null;

        for (DataSet ds : mDataSets) {
            if (ds.getDataType().equals(DataType.TYPE_DISTANCE_DELTA)) {
                mDistanceDataPoints = ds.getDataPoints();
            } else if (ds.getDataType().equals(DataType.TYPE_LOCATION_SAMPLE)) {
                mLocationDataPoints = ds.getDataPoints();
            } else if (ds.getDataType().equals(DataType.TYPE_ACTIVITY_SEGMENT)) {
                mSegmentDataPoints = ds.getDataPoints();
            } else if (ds.getDataType().equals(DataType.AGGREGATE_ACTIVITY_SUMMARY)) {
                if (ds.getDataPoints().size() > 0) {
                    mActiveTime = ds.getDataPoints().get(0).getValue(Field.FIELD_DURATION).asInt();
                }
            }
        }
    }

    private void fillMap(boolean fillMap) {
        final MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragment_session_map));

        if (fillMap && mapFragment != null) {
            if (mapFragment.getView() != null)
                mapFragment.getView().setVisibility(View.VISIBLE);

            mapFragment.getMapAsync(googleMap -> {
                map = googleMap;
                map.clear();
                map.setPadding(40, 80, 40, 0);
                map.setMyLocationEnabled(false);
                map.getUiSettings().setZoomControlsEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);

                Observable.just(map)
                        .subscribeOn(Schedulers.newThread())
                        .subscribe(new Subscriber<GoogleMap>() {
                            private SessionMapData mapData;

                            @Override
                            public void onCompleted() {
                                Observable.just(map)
                                        .subscribeOn(Schedulers.newThread())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(result -> {
                                            mapData.setDataIntoMap(result, mapData);
                                            dismissProgress();
                                        });
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "Error getting Datapoints: set map polylines");
                            }

                            @Override
                            public void onNext(GoogleMap googleMap) {
                                mapData = new SessionMapData();
                                mapData.readMapData(mSegmentDataPoints, mLocationDataPoints);
                            }
                        });
            });
        } else {
            if (mapFragment != null && mapFragment.getView() != null)
                mapFragment.getView().setVisibility(View.GONE);
        }
    }

    @Override
    public void setSessionData(SessionReadResult sessionReadResult) {
        if (sessionReadResult != null && sessionReadResult.getSessions().size() > 0) {
            mDataSets = sessionReadResult.getDataSet(mSession);

            if (mSession.getAppPackageName().equals(BuildConfig.APPLICATION_ID)) {
                mDeleteButton.setVisible(true);
            }

            fillViewContent();
            dismissProgress();
        }
    }
}