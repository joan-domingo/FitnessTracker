package cat.xojan.fittracker.googlefit;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.DataDeleteRequest;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.google.android.gms.fitness.request.SessionReadRequest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cat.xojan.fittracker.Constant;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.Utils;
import cat.xojan.fittracker.session.SessionFragment;
import cat.xojan.fittracker.session.SessionListFragment;
import cat.xojan.fittracker.workout.DistanceController;
import cat.xojan.fittracker.workout.TimeController;

public class FitnessController {

    private static FitnessController instance = null;
    private List<Session> mReadSessions;
    private Session mSingleSession;
    private List<DataSet> mSingleSessionDataSets;
    private String mFitnessActivity;
    private int mNumSegments;
    private DataSource mSpeedDataSource;
    private DataSet mSpeedDataSet;
    private DataSource mDistanceDataSource;
    private DataSet mDistanceDataSet;
    private DataSource mSummaryDataSource;
    private DataSource mAggregateSpeedDataSource;
    private DataSource mLocationDataSource;
    private DataSet mLocationDataSet;

    protected FitnessController() {
    }

    public static FitnessController getInstance() {
        if (instance == null) {
            instance = new FitnessController();
        }
        return instance;
    }

    private static final int REQUEST_OAUTH = 1;
    private static final String AUTH_PENDING = "auth_state_pending";

    private boolean authInProgress = false;
    private GoogleApiClient mClient = null;

    private Context context;
    private Activity activity;

    public void setVars(Activity activity) {
        this.context = activity.getBaseContext();
        this.activity = activity;
    }

    public void setAuthInProgress(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }
    }

    public void connect() {
        // Connect to the Fitness API
        Log.i(Constant.TAG, "Connecting...");
        mClient.connect();
    }

    public void disconnect() {
        if (mClient.isConnected()) {
            mClient.disconnect();
        }
    }

    public void onActivityResult(int requestCode, int resultCode) {
        if (requestCode == REQUEST_OAUTH) {
            authInProgress = false;
            if (resultCode == activity.RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mClient.isConnecting() && !mClient.isConnected()) {
                    mClient.connect();
                }
            }
        }
    }

    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }

    public void init() {
        if (mClient != null && mClient.isConnected()) {
            SessionListFragment.getHandler().sendEmptyMessage(Constant.GOOGLE_API_CLIENT_CONNECTED);
            return;
        }
        mClient = new GoogleApiClient.Builder(context)
                .addApi(Fitness.API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.i(Constant.TAG, "Connected!!!");
                                SessionListFragment.getHandler().sendEmptyMessage(Constant.GOOGLE_API_CLIENT_CONNECTED);
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
                        new GoogleApiClient.OnConnectionFailedListener() {
                            // Called whenever the API client fails to connect.
                            @Override
                            public void onConnectionFailed(ConnectionResult result) {
                                Log.i(Constant.TAG, "Connection failed. Cause: " + result.toString());
                                if (!result.hasResolution()) {
                                    // Show the localized error dialog
                                    GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
                                            activity, 0).show();
                                    return;
                                }
                                // The failure has a resolution. Resolve it.
                                // Called typically when the app is not yet authorized, and an
                                // authorization dialog is displayed to the user.
                                if (!authInProgress) {
                                    try {
                                        Log.i(Constant.TAG, "Attempting to resolve failed connection");
                                        authInProgress = true;
                                        result.startResolutionForResult(activity,
                                                REQUEST_OAUTH);
                                    } catch (IntentSender.SendIntentException e) {
                                        Log.e(Constant.TAG,
                                                "Exception while starting resolution activity", e);
                                    }
                                }
                            }
                        }
                )
                .build();
    }

    public void readLastSessions(long startTime, long endTime) {
        // Build a session read request
        SessionReadRequest readRequest = new SessionReadRequest.Builder()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .readSessionsFromAllApps()
                .build();

        new SessionReader(mClient) {

            public void getSessionList(List<Session> sessions) {
                Collections.reverse(sessions);
                mReadSessions = sessions;
                SessionListFragment.getHandler().sendEmptyMessage(Constant.MESSAGE_SESSIONS_READ);
            }

        }.execute(readRequest);
    }

    public List<Session> getReadSessions() {
        return mReadSessions;
    }

    public void saveSession(final FragmentActivity fragmentActivity, String name, String description) {
        //summary activity (aggregate)
        DataPoint summaryDataPoint = DataPoint.create(mSummaryDataSource);
        summaryDataPoint.setTimeInterval(TimeController.getInstance().getSessionStartTime(), TimeController.getInstance().getSessionEndTime(), TimeUnit.MILLISECONDS);
        summaryDataPoint.getValue(Field.FIELD_NUM_SEGMENTS).setInt(mNumSegments);

        //summary speed (aggregate)
        DataPoint summarySpeedDataPoint = DataPoint.create(mAggregateSpeedDataSource);
        summarySpeedDataPoint.setTimeInterval(TimeController.getInstance().getSessionStartTime(), TimeController.getInstance().getSessionEndTime(), TimeUnit.MILLISECONDS);
        summarySpeedDataPoint.getValue(Field.FIELD_AVERAGE).setFloat(insertAggregateSpeed());

        // Create a session with metadata about the activity.
        Session session = new Session.Builder()
                .setName(name)
                .setDescription(description)
                .setIdentifier(Constant.PACKAGE_SPECIFIC_PART + ":" + TimeController.getInstance().getSessionStartTime())
                .setActivity(mFitnessActivity)
                .setStartTime(TimeController.getInstance().getSessionStartTime(), TimeUnit.MILLISECONDS)
                .setEndTime(TimeController.getInstance().getSessionEndTime(), TimeUnit.MILLISECONDS)
                .build();

        // Build a session insert request
        SessionInsertRequest insertRequest = new SessionInsertRequest.Builder()
                .setSession(session)
                .addAggregateDataPoint(summaryDataPoint)
                .addAggregateDataPoint(summarySpeedDataPoint)
                .addDataSet(mSpeedDataSet)
                .addDataSet(mDistanceDataSet)
                .addDataSet(mLocationDataSet)
                .build();

        //TODO: crash when no data points

        new SessionWriter(mClient) {

            public void onFinishSessionWriting() {
                fragmentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new SessionListFragment())
                        .commit();
            }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, insertRequest);
    }

    private float insertAggregateSpeed() {
        long timeInSeconds = TimeController.getInstance().getSessionTotalTime() / 1000;
        float distanceInMeters = DistanceController.getInstance().getSessionDistance();

        return distanceInMeters/timeInSeconds;
    }

    public void readSession(String sessionId, long startTime, long endTime) {
        // Build a session read request
        SessionReadRequest readRequest = new SessionReadRequest.Builder()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .setSessionId(sessionId)
                .read(DataType.AGGREGATE_ACTIVITY_SUMMARY)
                .read(DataType.AGGREGATE_SPEED_SUMMARY)
                .read(DataType.TYPE_SPEED)
                .read(DataType.TYPE_DISTANCE_DELTA)
                .read(DataType.TYPE_LOCATION_SAMPLE)
                .readSessionsFromAllApps()
                .build();

        new SessionReader(mClient) {

            public void getSessionDataSets(Session session, List<DataSet> dataSets) {
                mSingleSession = session;
                mSingleSessionDataSets = dataSets;
                // Process the data sets for this session
                /*for (DataSet dataSet : dataSets) {
                    dumpDataSet(dataSet);
                }*/
                SessionFragment.getHandler().sendEmptyMessage(Constant.MESSAGE_SINGLE_SESSION_READ);
            }

        }.execute(readRequest);
    }

    public Session getSingleSession() {
        return mSingleSession;
    }

    public List<DataSet> getSingleSessionDataSets() {
        return mSingleSessionDataSets;
    }

    private void dumpDataSet(DataSet dataSet) {
        Log.i(Constant.TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.i(Constant.TAG, "Data point:");
            Log.i(Constant.TAG, "\tType: " + dp.getDataType().getName());
            Log.i(Constant.TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i(Constant.TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            for (Field field : dp.getDataType().getFields()) {
                Log.i(Constant.TAG, "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));
            }
        }
    }

    public void setFitnessActivity(String activity) {
        mFitnessActivity = activity;
    }

    public void deleteSession(Session session) {
        //  Create a delete request object, providing a data type and a time interval
        DataDeleteRequest request = new DataDeleteRequest.Builder()
                .addSession(session)
                .deleteAllData()
                .setTimeInterval(session.getStartTime(TimeUnit.MILLISECONDS),
                        session.getEndTime(TimeUnit.MILLISECONDS),
                        TimeUnit.MILLISECONDS)
                .build();

        // Invoke the History API with the Google API client object and delete request, and then
        // specify a callback that will check the result.
        Fitness.HistoryApi.deleteData(mClient, request)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.i(Constant.TAG, "Successfully deleted data");
                        } else {
                            // The deletion will fail if the requesting app tries to delete data
                            // that it did not insert.
                            Log.i(Constant.TAG, "Failed to delete data");
                        }
                        SessionFragment.getHandler().sendEmptyMessage(Constant.MESSAGE_SESSION_DELETED);
                    }
                });
    }

    public void saveSegment() {
        long endTimeSegment = TimeController.getInstance().getSegmentEndTime();
        long startTimeSegment = TimeController.getInstance().getSegmentStartTime();

        //segment
        mNumSegments++;

        //speed
        DataPoint speedDataPoint = DataPoint.create(mSpeedDataSource);
        speedDataPoint.setTimeInterval(startTimeSegment, endTimeSegment, TimeUnit.MILLISECONDS);
        speedDataPoint.getValue(Field.FIELD_SPEED).setFloat(insertSegmentSpeed());
        mSpeedDataSet.add(speedDataPoint);

        //distance
        DataPoint distanceDataPoint = DataPoint.create(mDistanceDataSource);
        distanceDataPoint.setTimeInterval(startTimeSegment, endTimeSegment, TimeUnit.MILLISECONDS);
        distanceDataPoint.getValue(Field.FIELD_DISTANCE).setFloat(DistanceController.getInstance().getSegmentDistance());
        mDistanceDataSet.add(distanceDataPoint);
    }

    private float insertSegmentSpeed() {
        long timeInSeconds = TimeController.getInstance().getSegmentTime() / 1000;
        float distanceInMeters = DistanceController.getInstance().getSegmentDistance();

        return distanceInMeters/timeInSeconds;
    }

    public void start() {
        //segments
        mNumSegments = 0;

        //speed
        mSpeedDataSource = new DataSource.Builder()
                .setAppPackageName(context)
                .setDataType(DataType.TYPE_SPEED)
                .setType(DataSource.TYPE_RAW)
                .build();
        mSpeedDataSet = DataSet.create(mSpeedDataSource);

        //distance
        mDistanceDataSource = new DataSource.Builder()
                .setAppPackageName(context)
                .setDataType(DataType.TYPE_DISTANCE_DELTA)
                .setType(DataSource.TYPE_RAW)
                .build();
        mDistanceDataSet = DataSet.create(mDistanceDataSource);

        //session summary
        mSummaryDataSource = new DataSource.Builder()
                .setAppPackageName(context)
                .setDataType(DataType.AGGREGATE_ACTIVITY_SUMMARY)
                .setType(DataSource.TYPE_RAW)
                .build();

        //speed summary
        mAggregateSpeedDataSource = new DataSource.Builder()
                .setAppPackageName(context)
                .setDataType(DataType.AGGREGATE_SPEED_SUMMARY)
                .setType(DataSource.TYPE_RAW)
                .build();

        //location
        mLocationDataSource = new DataSource.Builder()
                .setAppPackageName(context)
                .setDataType(DataType.TYPE_LOCATION_SAMPLE)
                .setType(DataSource.TYPE_RAW)
                .build();
        mLocationDataSet = DataSet.create(mLocationDataSource);
    }

    public void storeLocation(Location location) {
        long time = Calendar.getInstance().getTimeInMillis();
        //distance
        DataPoint locationDataPoint = DataPoint.create(mLocationDataSource);
        locationDataPoint.setTimeInterval(time, time, TimeUnit.MILLISECONDS);
        locationDataPoint.getValue(Field.FIELD_LATITUDE).setFloat((float) location.getLatitude());
        locationDataPoint.getValue(Field.FIELD_LONGITUDE).setFloat((float) location.getLongitude());
        locationDataPoint.getValue(Field.FIELD_ACCURACY).setFloat(location.getAccuracy());
        locationDataPoint.getValue(Field.FIELD_ALTITUDE).setFloat((float) location.getAltitude());
        mLocationDataSet.add(locationDataPoint);
    }

    public String getFitnessActivity() {
        return mFitnessActivity;
    }
}
