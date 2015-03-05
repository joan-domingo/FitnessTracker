package cat.xojan.fittracker.session;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.DataDeleteRequest;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.SessionReadResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cat.xojan.fittracker.Constant;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.util.SessionDetailedDataLoader;
import cat.xojan.fittracker.util.Utils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SessionActivity extends ActionBarActivity {

    private ProgressBar mProgressBar;
    private LinearLayout mSessionView;
    private Session mSession;
    private MenuItem mDeleteButton;
    private List<DataSet> mDataSets;
    private List<DataPoint> mLocationDataPoints;
    private List<DataPoint> mSegmentDataPoints;
    private GoogleApiClient mClient;
    private long mStartTime;
    private long mEndTime;
    private String mSessionIdentifier;
    private boolean authInProgress = false;
    private GoogleMap map;
    private List<DataPoint> mDistanceDataPoints;
    private int mActiveTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(Constant.AUTH_PENDING);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.fragment_session_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProgressBar = (ProgressBar) findViewById(R.id.fragment_loading_spinner);
        mSessionView = (LinearLayout) findViewById(R.id.fragment_session_container);
        showProgressBar(true);

        /**
         * session contains:
         * name, description, identifier, package name, activity type, start time, end time
         */

        Intent intent = getIntent();

        // Get the intent extras
        /*long startTime = Fitness.getStartTime(intent, TimeUnit.MILLISECONDS);
        long endTime = Fitness.getEndTime(intent, TimeUnit.MILLISECONDS);
        Session session = Session.extract(intent); TODO not working so far*/
        mStartTime = intent.getLongExtra(Constant.EXTRA_START, 0);
        mEndTime = intent.getLongExtra(Constant.EXTRA_END, 0);
        mSessionIdentifier = intent.getStringExtra(Constant.EXTRA_SESSION);

        // Show the session in your app
        buildFitnessClient();
    }

    private void buildFitnessClient() {
        mClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.i(Constant.TAG, "Connected!!!");
                                readSessionDataSets();
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and react to it here.
                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.i(Constant.TAG, "Connection lost.  Cause: Network Lost.");
                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.i(Constant.TAG, "Connection lost.  Reason: Service Disconnected");
                                }
                            }
                        }
                )
                .addOnConnectionFailedListener(
                        result -> {
                            Log.i(Constant.TAG, "Connection failed. Cause: " + result.toString());
                            if (!result.hasResolution()) {
                                // Show the localized error dialog
                                GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
                                        SessionActivity.this, 0).show();
                                return;
                            }
                            // The failure has a resolution. Resolve it.
                            // Called typically when the app is not yet authorized, and an
                            // authorization dialog is displayed to the user.
                            if (!authInProgress) {
                                try {
                                    Log.i(Constant.TAG, "Attempting to resolve failed connection");
                                    authInProgress = true;
                                    result.startResolutionForResult(SessionActivity.this,
                                            Constant.REQUEST_OAUTH);
                                } catch (IntentSender.SendIntentException e) {
                                    Log.e(Constant.TAG,
                                            "Exception while starting resolution activity", e);
                                }
                            }
                        }
                )
                .build();
    }

    private void readSessionDataSets() {
        //create read request
        SessionReadRequest readRequest = new SessionReadRequest.Builder()
                .setTimeInterval(mStartTime, mEndTime, TimeUnit.MILLISECONDS)
                .setSessionId(mSessionIdentifier)
                .read(DataType.AGGREGATE_ACTIVITY_SUMMARY)
                .read(DataType.TYPE_DISTANCE_DELTA)
                .read(DataType.TYPE_LOCATION_SAMPLE)
                .read(DataType.TYPE_ACTIVITY_SEGMENT)
                .readSessionsFromAllApps()
                .build();

        //read session and datasets
        Observable.just(readRequest)
                .subscribeOn(Schedulers.newThread())
                .subscribe(request -> {
                    SessionReadResult sessionReadResult = Fitness.SessionsApi
                            .readSession(mClient, readRequest)
                            .await(1, TimeUnit.MINUTES);

                    if (sessionReadResult != null && sessionReadResult.getSessions().size() > 0) {
                        mSession = sessionReadResult.getSessions().get(0);
                        mDataSets = sessionReadResult.getDataSet(mSession);

                        Observable.just(mSession)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(s -> {
                                    if (s.getAppPackageName().equals(Constant.PACKAGE_SPECIFIC_PART))
                                        mDeleteButton.setVisible(true);
                                    fillViewContent();
                                });
                    }
                });
    }

    private void showProgressBar(boolean b) {
        if (b) {
            mProgressBar.setVisibility(View.VISIBLE);
            mSessionView.setVisibility(View.GONE);
        }
        else {
            mProgressBar.setVisibility(View.GONE);
            mSessionView.setVisibility(View.VISIBLE);
        }
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
                            deleteSession();
                            showProgressBar(true);
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

    private void deleteSession() {
        //  Create a delete request object, providing a data type and a time interval
        DataDeleteRequest request = new DataDeleteRequest.Builder()
                .addSession(mSession)
                .deleteAllData()
                .setTimeInterval(mSession.getStartTime(TimeUnit.MILLISECONDS),
                        mSession.getEndTime(TimeUnit.MILLISECONDS),
                        TimeUnit.MILLISECONDS)
                .build();

        // Invoke the History API with the Google API client object and delete request, and then
        // specify a callback that will check the result.
        Observable.just(request)
                .subscribeOn(Schedulers.newThread())
                .subscribe(r -> {
                    Fitness.HistoryApi.deleteData(mClient, request)
                            .setResultCallback(status -> {
                                if (status.isSuccess()) {
                                    Log.i(Constant.TAG, "Successfully deleted data");
                                } else {
                                    // The deletion will fail if the requesting app tries to delete data
                                    // that it did not insert.
                                    Log.i(Constant.TAG, "Failed to delete data");
                                }
                            });
                    finish();
                });
    }

    private void fillViewContent() {

        getDataPoints();

        new SessionDetailedDataLoader(this) {

            public void onResult(LinearLayout intervalView, double totalDistance) {
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
                ((TextView)findViewById(R.id.fragment_session_total_distance))
                        .setText(Utils.getRightDistance((float) totalDistance, SessionActivity.this));
                //name
                ((TextView) findViewById(R.id.fragment_session_name))
                        .setText(mSession.getName());
                //description
                ((TextView)findViewById(R.id.fragment_session_description))
                        .setText(mSession.getDescription());
                //date
                ((TextView)findViewById(R.id.fragment_session_date))
                        .setText(Utils.getRightDate(mSession.getStartTime(TimeUnit.MILLISECONDS), SessionActivity.this));
                //start time
                ((TextView)findViewById(R.id.fragment_session_start))
                        .setText(Utils.millisToTime(mSession.getStartTime(TimeUnit.MILLISECONDS)));
                //end time
                ((TextView)findViewById(R.id.fragment_session_end))
                        .setText(Utils.millisToTime(mSession.getEndTime(TimeUnit.MILLISECONDS)));
                //total/duration time
                ((TextView)findViewById(R.id.fragment_session_total_time))
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
                showProgressBar(false);
                if (mLocationDataPoints != null && mLocationDataPoints.size() > 0) {
                    fillMap(true);
                } else {
                    fillMap(false);
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                mLocationDataPoints,
                mSegmentDataPoints);
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
                if (ds.getDataPoints() != null && ds.getDataPoints().size() > 0) {
                    mActiveTime = ds.getDataPoints().get(0).getValue(Field.FIELD_DURATION).asInt();
                }
            }
        }
    }

    private void fillMap(boolean fillMap) {
        final MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.fragment_session_map));

        if (fillMap) {
            mapFragment.getView().setVisibility(View.VISIBLE);

            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    map = googleMap;
                    map.clear();
                    map.setPadding(40, 80, 40, 0);
                    map.setMyLocationEnabled(false);
                    map.getUiSettings().setZoomControlsEnabled(false);
                    map.getUiSettings().setMyLocationButtonEnabled(false);
                }
            });

                new MapLoader(map) {
                    public void onResult(final List<PolylineOptions> mPolyList, final List<MarkerOptions> mMarkerList, final LatLngBounds.Builder mBoundsBuilder) {
                        map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                            @Override
                            public void onMapLoaded() {
                                map.moveCamera(CameraUpdateFactory.newLatLngBounds(mBoundsBuilder.build(), 5));

                                for (PolylineOptions pl : mPolyList ) {
                                    map.addPolyline(pl
                                            .geodesic(true)
                                            .width(6)
                                            .color(Color.BLACK));
                                }
                                for (MarkerOptions mo : mMarkerList) {
                                    map.addMarker(mo);
                                }
                            }
                        });
                    }
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,
                        mLocationDataPoints, mSegmentDataPoints);

        } else {
            if (mapFragment.getView() != null)
                mapFragment.getView().setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect to the Fitness API
        Log.i(Constant.TAG, "Connecting...");
        mClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mClient.isConnected()) {
            mClient.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constant.REQUEST_OAUTH) {
            authInProgress = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mClient.isConnecting() && !mClient.isConnected()) {
                    mClient.connect();
                }
            } else if (!mClient.isConnected()) {
                finish();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Constant.AUTH_PENDING, authInProgress);
    }
}
