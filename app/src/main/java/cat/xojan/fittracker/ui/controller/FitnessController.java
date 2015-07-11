package cat.xojan.fittracker.ui.controller;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.SessionInsertRequest;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cat.xojan.fittracker.BuildConfig;

public class FitnessController {

    private static final String PACKAGE_SPECIFIC_PART = BuildConfig.APPLICATION_ID;
    private final TimeController timeController;

    private GoogleApiClient mClient;
    private final Context mContext;
    private int mNumSegments;
    private DataSource mSpeedDataSource;
    private DataSet mSpeedDataSet;
    private DataSource mDistanceDataSource;
    private DataSet mDistanceDataSet;
    private DataSource mSummaryDataSource;
    private DataSource mLocationDataSource;
    private DataSet mLocationDataSet;
    private DataSource mSegmentDataSource;
    private DataSet mSegmentDataSet;

    public FitnessController(Context mContext, TimeController timeController) {
        this.mContext = mContext;
        this.timeController = timeController;
    }

    public SessionInsertRequest saveSession(String name, String description,
                                            double totalDistance, String fitnessActivity) {
        //summary activity (aggregate)
        DataPoint summaryDataPoint = DataPoint.create(mSummaryDataSource);
        summaryDataPoint.setTimeInterval(timeController.getSessionStartTime(),
                timeController.getSessionEndTime(), TimeUnit.MILLISECONDS);
        summaryDataPoint.getValue(Field.FIELD_NUM_SEGMENTS).setInt(mNumSegments);
        summaryDataPoint.getValue(Field.FIELD_DURATION).setInt((int) timeController
                .getSessionWorkoutTime());
        summaryDataPoint.getValue(Field.FIELD_ACTIVITY).setActivity(fitnessActivity);

        //distance
        DataPoint distanceDataPoint = DataPoint.create(mDistanceDataSource);
        distanceDataPoint.setTimeInterval(timeController.getSessionStartTime(),
                timeController.getSessionEndTime(), TimeUnit.MILLISECONDS);
        distanceDataPoint.getValue(Field.FIELD_DISTANCE).setFloat((float) totalDistance);
        mDistanceDataSet.add(distanceDataPoint);

        // Create a session with metadata about the activity.
        Session session = new Session.Builder()
                .setName(name)
                .setDescription(description)
                .setIdentifier(PACKAGE_SPECIFIC_PART + ":"
                        + timeController.getSessionStartTime())
                .setActivity(fitnessActivity)
                .setStartTime(timeController.getSessionStartTime(), TimeUnit.MILLISECONDS)
                .setEndTime(timeController.getSessionEndTime(), TimeUnit.MILLISECONDS)
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

        return insertRequestBuilder.build();
    }

    public void saveSegment(boolean isPauseSegment) {
        long endTimeSegment;
        long startTimeSegment;

        if (isPauseSegment) {
            endTimeSegment = timeController.getSegmentStartTime();
            startTimeSegment = timeController.getSegmentEndTime();
        } else {
            endTimeSegment = timeController.getSegmentEndTime();
            startTimeSegment = timeController.getSegmentStartTime();
        }

        //segment
        mNumSegments++;
        DataPoint segmentDataPoint = DataPoint.create(mSegmentDataSource);
        segmentDataPoint.setTimeInterval(startTimeSegment, endTimeSegment, TimeUnit.MILLISECONDS);
        //segmentDataPoint.getValue(Field.FIELD_ACTIVITY).setActivity(mFitnessActivity);
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
}
