package cat.xojan.fittracker.controller;

import android.content.Context;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.google.android.gms.fitness.request.SessionReadRequest;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cat.xojan.fittracker.Constant;
import cat.xojan.fittracker.R;
import cat.xojan.fittracker.sessionlist.SessionListFragment;
import cat.xojan.fittracker.workout.TimeController;
import rx.Observable;
import rx.schedulers.Schedulers;

public class FitnessController {

    private GoogleApiClient mClient;
    private final Context mContext;
    private String mFitnessActivity;
    private int mNumSegments;
    private DataSource mSpeedDataSource;
    private DataSet mSpeedDataSet;
    private DataSource mDistanceDataSource;
    private DataSet mDistanceDataSet;
    private DataSource mSummaryDataSource;
    private DataSource mLocationDataSource;
    private DataSet mLocationDataSet;
    private Calendar mSessionListStartDate;
    private Calendar mSessionListEndDate;
    private DataSource mSegmentDataSource;
    private DataSet mSegmentDataSet;

    public FitnessController(Context mContext) {
        this.mContext = mContext;
        mSessionListStartDate = getStartDate();
        mSessionListEndDate = Calendar.getInstance();
    }

    private Calendar getStartDate() {
        Calendar date = Calendar.getInstance();
        date.add(Calendar.MONTH, -1);
        return date;
    }

    public void saveSession(final FragmentActivity fragmentActivity, String name, String description, double totalDistance) {
        //summary activity (aggregate)
        DataPoint summaryDataPoint = DataPoint.create(mSummaryDataSource);
        summaryDataPoint.setTimeInterval(TimeController.getInstance().getSessionStartTime(), TimeController.getInstance().getSessionEndTime(), TimeUnit.MILLISECONDS);
        summaryDataPoint.getValue(Field.FIELD_NUM_SEGMENTS).setInt(mNumSegments);
        summaryDataPoint.getValue(Field.FIELD_DURATION).setInt((int) TimeController.getInstance().getSessionWorkoutTime());
        summaryDataPoint.getValue(Field.FIELD_ACTIVITY).setActivity(mFitnessActivity);

        //distance
        DataPoint distanceDataPoint = DataPoint.create(mDistanceDataSource);
        distanceDataPoint.setTimeInterval(TimeController.getInstance().getSessionStartTime(),
                TimeController.getInstance().getSessionEndTime(), TimeUnit.MILLISECONDS);
        distanceDataPoint.getValue(Field.FIELD_DISTANCE).setFloat((float) totalDistance);
        mDistanceDataSet.add(distanceDataPoint);

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

        Observable.just(insertRequest)
                .subscribeOn(Schedulers.newThread())
                .subscribe(request -> {
                    Status insertStatus = Fitness.SessionsApi.insertSession(mClient, insertRequest)
                            .await(1, TimeUnit.MINUTES);

                    if (!insertStatus.isSuccess()) {
                        Log.i(Constant.TAG, "There was a problem inserting the session: " +
                                insertStatus.getStatusMessage());
                    } else {
                        Log.i(Constant.TAG, "Session insert was successful!");
                        // At this point, the session has been inserted and can be read.
                        setEndTime(Calendar.getInstance());

                        fragmentActivity.getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container, new SessionListFragment())
                                .commit();
                    }

                });
    }

    public void setFitnessActivity(String activity) {
        mFitnessActivity = activity;
    }

    public void saveSegment(boolean isPauseSegment) {
        long endTimeSegment;
        long startTimeSegment;

        if (isPauseSegment) {
            endTimeSegment = TimeController.getInstance().getSegmentStartTime();
            startTimeSegment = TimeController.getInstance().getSegmentEndTime();
        } else {
            endTimeSegment = TimeController.getInstance().getSegmentEndTime();
            startTimeSegment = TimeController.getInstance().getSegmentStartTime();
        }

        //segment
        mNumSegments++;
        DataPoint segmentDataPoint = DataPoint.create(mSegmentDataSource);
        segmentDataPoint.setTimeInterval(startTimeSegment, endTimeSegment, TimeUnit.MILLISECONDS);
        segmentDataPoint.getValue(Field.FIELD_ACTIVITY).setActivity(mFitnessActivity);
        mSegmentDataSet.add(segmentDataPoint);
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

    public void saveSpeed(long start, long end, double speed) {
        //speed
        DataPoint speedDataPoint = DataPoint.create(mSpeedDataSource);
        speedDataPoint.setTimeInterval(start, end, TimeUnit.MILLISECONDS);
        speedDataPoint.getValue(Field.FIELD_SPEED).setFloat((float) speed);
        mSpeedDataSet.add(speedDataPoint);
    }

    public void setClient(GoogleApiClient mClient) {
        this.mClient = mClient;
    }

    public GoogleApiClient getClient() {
        return mClient;
    }

    public SessionReadRequest getSessionsReadRequest() {
        return new SessionReadRequest.Builder()
                .setTimeInterval(getStartTime(),
                        getEndTime(),
                        TimeUnit.MILLISECONDS)
                .read(DataType.TYPE_DISTANCE_DELTA)
                .read(DataType.TYPE_ACTIVITY_SEGMENT)
                .readSessionsFromAllApps()
                .enableServerQueries()
                .build();
    }
}
