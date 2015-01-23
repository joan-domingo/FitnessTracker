package cat.xojan.fittracker.googlefit;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
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
import com.google.android.gms.fitness.result.SessionReadResult;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cat.xojan.fittracker.Constant;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.session.SessionActivity;
import cat.xojan.fittracker.sessionlist.SessionListFragment;
import cat.xojan.fittracker.workout.DistanceController;
import cat.xojan.fittracker.workout.TimeController;

public class FitnessController {

    private static FitnessController instance = null;
    private String mFitnessActivity;
    private int mNumSegments;
    private DataSource mSpeedDataSource;
    private DataSet mSpeedDataSet;
    private DataSource mDistanceDataSource;
    private DataSet mDistanceDataSet;
    private DataSource mSummaryDataSource;
    private DataSource mLocationDataSource;
    private DataSet mLocationDataSet;
    private Calendar mSessionListStartDate = getStartDate();
    private Calendar mSessionListEndDate = Calendar.getInstance();
    private DataSource mSegmentDataSource;
    private DataSet mSegmentDataSet;
    private SessionReadResult mSessionReadResult;
    private Session mSingleSessionResult;
    private List<DataSet> mSingleSessionDataSets;

    private Calendar getStartDate() {
        Calendar date = Calendar.getInstance();
        date.add(Calendar.MONTH, -1);

        return date;
    }

    protected FitnessController() {
    }

    public static FitnessController getInstance() {
        if (instance == null) {
            instance = new FitnessController();
        }
        return instance;
    }
    private GoogleApiClient mClient = null;

    private Context mContext;

    public void setVars(Context context, GoogleApiClient client) {
        mContext = context;
        mClient = client;
    }

    public void readSessions() {
        // Build a session read request
        SessionReadRequest readRequest = new SessionReadRequest.Builder()
                .setTimeInterval(mSessionListStartDate.getTimeInMillis(), mSessionListEndDate.getTimeInMillis(), TimeUnit.MILLISECONDS)
                .read(DataType.AGGREGATE_ACTIVITY_SUMMARY)
                .read(DataType.TYPE_DISTANCE_DELTA)
                .read(DataType.TYPE_ACTIVITY_SEGMENT)
                .readSessionsFromAllApps()
                .enableServerQueries()
                .build();

        new SessionReader(mClient) {

            public void getSessionList(SessionReadResult sessionReadResult) {
                mSessionReadResult = sessionReadResult;
                SessionListFragment.getHandler().sendEmptyMessage(Constant.MESSAGE_READ_SESSIONS);
            }

        }.execute(readRequest);
    }

    public void saveSession(final FragmentActivity fragmentActivity, String name, String description) {
        //summary activity (aggregate)
        DataPoint summaryDataPoint = DataPoint.create(mSummaryDataSource);
        summaryDataPoint.setTimeInterval(TimeController.getInstance().getSessionStartTime(), TimeController.getInstance().getSessionEndTime(), TimeUnit.MILLISECONDS);
        summaryDataPoint.getValue(Field.FIELD_NUM_SEGMENTS).setInt(mNumSegments);
        summaryDataPoint.getValue(Field.FIELD_DURATION).setInt((int) TimeController.getInstance().getSessionWorkoutTime());
        summaryDataPoint.getValue(Field.FIELD_ACTIVITY).setActivity(mFitnessActivity);

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
        SessionInsertRequest.Builder insertRequestBuilder = new SessionInsertRequest.Builder();
        insertRequestBuilder.setSession(session)
                .addAggregateDataPoint(summaryDataPoint)
                .addDataSet(mDistanceDataSet)
                .addDataSet(mLocationDataSet)
                .addDataSet(mSegmentDataSet);

        if (mSpeedDataSet.getDataPoints().size() > 0) {
            insertRequestBuilder.setSession(session)
                    .addDataSet(mSpeedDataSet);
        }

        SessionInsertRequest insertRequest = insertRequestBuilder.build();

        new SessionWriter(mClient) {

            public void onFinishSessionWriting() {
                setEndTime(Calendar.getInstance());
                fragmentActivity.getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new SessionListFragment())
                        .commit();
            }

        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, insertRequest);
    }

    /*public void dumpDataSet(DataSet dataSet) {
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
    }*/

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
                        SessionActivity.getHandler().sendEmptyMessage(Constant.MESSAGE_SESSION_DELETED);
                    }
                });
    }

    public void saveSegment() {
        long endTimeSegment = TimeController.getInstance().getSegmentEndTime();
        long startTimeSegment = TimeController.getInstance().getSegmentStartTime();

        //segment
        mNumSegments++;
        DataPoint segmentDataPoint = DataPoint.create(mSegmentDataSource);
        segmentDataPoint.setTimeInterval(startTimeSegment, endTimeSegment, TimeUnit.MILLISECONDS);
        segmentDataPoint.getValue(Field.FIELD_ACTIVITY).setActivity(mFitnessActivity);
        mSegmentDataSet.add(segmentDataPoint);

        //distance
        DataPoint distanceDataPoint = DataPoint.create(mDistanceDataSource);
        distanceDataPoint.setTimeInterval(startTimeSegment, endTimeSegment, TimeUnit.MILLISECONDS);
        distanceDataPoint.getValue(Field.FIELD_DISTANCE).setFloat(DistanceController.getInstance().getSegmentDistance());
        mDistanceDataSet.add(distanceDataPoint);
    }

    public void start() {
        //segments
        mNumSegments = 0;

        //speed
        mSpeedDataSource = new DataSource.Builder()
                .setAppPackageName(mContext)
                .setDataType(DataType.TYPE_SPEED)
                .setType(DataSource.TYPE_RAW)
                .build();
        mSpeedDataSet = DataSet.create(mSpeedDataSource);

        //distance
        mDistanceDataSource = new DataSource.Builder()
                .setAppPackageName(mContext)
                .setDataType(DataType.TYPE_DISTANCE_DELTA)
                .setType(DataSource.TYPE_RAW)
                .build();
        mDistanceDataSet = DataSet.create(mDistanceDataSource);

        //session summary
        mSummaryDataSource = new DataSource.Builder()
                .setAppPackageName(mContext)
                .setDataType(DataType.AGGREGATE_ACTIVITY_SUMMARY)
                .setType(DataSource.TYPE_RAW)
                .build();

        //location
        mLocationDataSource = new DataSource.Builder()
                .setAppPackageName(mContext)
                .setDataType(DataType.TYPE_LOCATION_SAMPLE)
                .setType(DataSource.TYPE_RAW)
                .build();
        mLocationDataSet = DataSet.create(mLocationDataSource);

        //segment
        mSegmentDataSource = new DataSource.Builder()
                .setAppPackageName(mContext)
                .setDataType(DataType.TYPE_ACTIVITY_SEGMENT)
                .setType(DataSource.TYPE_RAW)
                .build();
        mSegmentDataSet = DataSet.create(mSegmentDataSource);
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

    public long getEndTime() {
        return mSessionListEndDate.getTimeInMillis();
    }

    public void setEndTime(Calendar calendar) {
        mSessionListEndDate = calendar;
    }

    public long getStartTime() {
        return mSessionListStartDate.getTimeInMillis();
    }

    public void setStartTime(Calendar calendar) {
        mSessionListStartDate = calendar;
    }

    public List<DataPoint> getSegmentDataPoints() {
        return mSegmentDataSet.getDataPoints();
    }

    public List<DataPoint> getLocationDataPoints() {
        return mLocationDataSet.getDataPoints();
    }

    public void saveSpeed(double speed) {
        long time = Calendar.getInstance().getTimeInMillis();
        //speed
        DataPoint speedDataPoint = DataPoint.create(mSpeedDataSource);
        speedDataPoint.setTimeInterval(time, time, TimeUnit.MILLISECONDS);
        speedDataPoint.getValue(Field.FIELD_SPEED).setFloat((float) speed);
        mSpeedDataSet.add(speedDataPoint);
    }

    public SessionReadResult getSessionReadResult() {
        return mSessionReadResult;
    }

    public void readSessionDataSets(long startTime, long endTime, String identifier) {
        SessionReadRequest readRequest = new SessionReadRequest.Builder()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .setSessionId(identifier)
                .read(DataType.AGGREGATE_ACTIVITY_SUMMARY)
                .read(DataType.AGGREGATE_SPEED_SUMMARY)
                .read(DataType.TYPE_SPEED)
                .read(DataType.TYPE_DISTANCE_DELTA)
                .read(DataType.TYPE_LOCATION_SAMPLE)
                .read(DataType.TYPE_ACTIVITY_SEGMENT)
                .read(DataType.AGGREGATE_DISTANCE_DELTA)
                .read(DataType.AGGREGATE_LOCATION_BOUNDING_BOX)
                .readSessionsFromAllApps()
                .build();

        new SessionReader(mClient) {

            public void getSessionList(SessionReadResult sessionReadResult) {
                if (sessionReadResult.getSessions().size() > 0) {
                    mSingleSessionResult = sessionReadResult.getSessions().get(0);
                    mSingleSessionDataSets = sessionReadResult.getDataSet(mSingleSessionResult);
                }
                SessionActivity.getHandler().sendEmptyMessage(Constant.MESSAGE_SINGLE_SESSION_READ);
            }

        }.execute(readRequest);
    }

    public Session getSingleSessionResult() {
        return mSingleSessionResult;
    }

    public List<DataSet> getSingleSessionDataSets() {
        return mSingleSessionDataSets;
    }
}
