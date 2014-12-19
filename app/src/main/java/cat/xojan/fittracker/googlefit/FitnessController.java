package cat.xojan.fittracker.googlefit;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.google.android.gms.fitness.request.SessionReadRequest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cat.xojan.fittracker.Constant;
import cat.xojan.fittracker.MainActivity;
import cat.xojan.fittracker.session.SessionFragment;
import cat.xojan.fittracker.session.SessionListFragment;
import cat.xojan.fittracker.workout.DistanceController;
import cat.xojan.fittracker.workout.TimeController;

/**
 * Created by Joan on 12/12/2014.
 */
public class FitnessController {

    private static FitnessController instance = null;
    private List<Session> mReadSessions;
    private Session mSingleSession;
    private List<DataSet> mSingleSessionDataSets;

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
        // Create the Google API Client
        mClient = new GoogleApiClient.Builder(context)
                .addApi(Fitness.API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.i(Constant.TAG, "Connected!!!");
                                MainActivity.getHandler().sendEmptyMessage(Constant.GOOGLE_API_CLIENT_CONNECTED);
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

    public void readLastSessions() {

        // Set a start and end time for our query, using a start time of 1 week before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -10);
        long startTime = cal.getTimeInMillis();

        // Build a session read request
        SessionReadRequest readRequest = new SessionReadRequest.Builder()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .read(DataType.TYPE_ACTIVITY_SEGMENT)
                .read(DataType.TYPE_SPEED)
                .readSessionsFromAllApps()
                .build();

        new SessionReader(mClient) {

            public void getSessionList(List<Session> sessions) {
                mReadSessions = sessions;
                MainActivity.getHandler().sendEmptyMessage(Constant.MESSAGE_SESSIONS_READ);
            }

        }.execute(readRequest);
    }

    public List<Session> getReadSessions() {
        return mReadSessions;
    }

    public void saveSession() {
        // Create a session with metadata about the activity.
        Session session = new Session.Builder()
                .setName("Session Name ") //TODO
                .setDescription("Session description ") //TODO
                .setIdentifier("" + Calendar.getInstance().getTimeInMillis()) //TODO
                .setActivity(FitnessActivities.RUNNING) //TODO
                .setStartTime(TimeController.getInstance().getStartTime(), TimeUnit.MILLISECONDS)
                .setEndTime(TimeController.getInstance().getEndTime(), TimeUnit.MILLISECONDS)
                .build();

        // Build a session insert request
        SessionInsertRequest insertRequest = new SessionInsertRequest.Builder()
                .setSession(session)
                .addDataSet(insertSpeedDataset())
                .build();

        new SessionWriter(mClient).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, insertRequest);
    }

    private DataSet insertSpeedDataset() {

        // Create a data source
        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(context)
                .setDataType(DataType.TYPE_SPEED)
                .setName(Constant.TAG_WORKOUT + " - speed")
                .setType(DataSource.TYPE_RAW)
                .build();

        // Create a data set
        DataSet dataSet = DataSet.create(dataSource);

        // For each data point, specify a start time, end time, and the data value
        DataPoint dataPoint = dataSet.createDataPoint()
                .setTimeInterval(TimeController.getInstance().getStartTime(),
                        TimeController.getInstance().getEndTime(), TimeUnit.MILLISECONDS);

        dataPoint.getValue(Field.FIELD_SPEED).setFloat(insertSpeed());



        dataSet.add(dataPoint);

        return dataSet;
    }

    private float insertSpeed() {

        long timeInMillis = TimeController.getInstance().getEndTime() - TimeController.getInstance().getStartTime();
        long timeInSeconds = timeInMillis / 1000;
        float distanceInMeters = DistanceController.getInstance().getSessionDistance();

        return distanceInMeters/timeInSeconds;
    }

    public void readSession(String sessionId, long startTime, long endTime) {
        // Build a session read request
        SessionReadRequest readRequest = new SessionReadRequest.Builder()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .setSessionId(sessionId)
                .read(DataType.TYPE_SPEED)
                .readSessionsFromAllApps()
                .build();

        new SessionReader(mClient) {

            public void getSessionDataSets(Session session, List<DataSet> dataSets) {
                mSingleSession = session;
                mSingleSessionDataSets = dataSets;
                // Process the data sets for this session
                for (DataSet dataSet : dataSets) {
                    dumpDataSet(dataSet);
                }
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
}
