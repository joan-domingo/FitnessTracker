package cat.xojan.fittracker.workout.controller;

import android.content.Context;
import android.location.Location;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.SessionInsertRequest;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import cat.xojan.fittracker.Constant;

public class FitnessController {

    private static FitnessController instance;
    private final TimeController mTimeController;
    private String mFitnessActivity;
    private int mNumSegments;
    private DataSource mDistanceDataSource;
    private DataSource mSegmentDataSource;
    private DataSource mSummaryDataSource;
    private DataSource mLocationDataSource;
    private DataSet mDistanceDataSet;
    private DataSet mLocationDataSet;
    private DataSet mSegmentDataSet;

    public static FitnessController getInstance() {
        if (instance == null) {
            instance = new FitnessController();
        }
        return instance;
    }

    public FitnessController() {
        mTimeController = TimeController.getInstance();
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

    public void setFitnessActivity(String activityType) {
        mFitnessActivity = activityType;
    }

    public void init(Context mContext) {
        //segments
        mNumSegments = 0;

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

    public SessionInsertRequest saveSession() {
        //summary activity (aggregate)
        DataPoint summaryDataPoint = DataPoint.create(mSummaryDataSource);
        summaryDataPoint.setTimeInterval(mTimeController.getSessionStartTime(), mTimeController.getSessionEndTime(), TimeUnit.MILLISECONDS);
        summaryDataPoint.getValue(Field.FIELD_NUM_SEGMENTS).setInt(mNumSegments);
        summaryDataPoint.getValue(Field.FIELD_DURATION).setInt((int) mTimeController.getSessionWorkoutTime());
        summaryDataPoint.getValue(Field.FIELD_ACTIVITY).setActivity(mFitnessActivity);

        //distance
        DataPoint distanceDataPoint = DataPoint.create(mDistanceDataSource);
        distanceDataPoint.setTimeInterval(mTimeController.getSessionStartTime(),
                mTimeController.getSessionEndTime(), TimeUnit.MILLISECONDS);
        distanceDataPoint.getValue(Field.FIELD_DISTANCE).setFloat((float) DistanceController.getInstance().getTotalDistance());
        mDistanceDataSet.add(distanceDataPoint);

        // Create a session with metadata about the activity.
        Session session = new Session.Builder()
                .setName("name")
                .setDescription("description")
                .setIdentifier(Constant.PACKAGE_SPECIFIC_PART + ":" + mTimeController.getSessionStartTime())
                .setActivity(mFitnessActivity)
                .setStartTime(mTimeController.getSessionStartTime(), TimeUnit.MILLISECONDS)
                .setEndTime(mTimeController.getSessionEndTime(), TimeUnit.MILLISECONDS)
                .build();

        // Build a session insert request
        SessionInsertRequest.Builder insertRequestBuilder = new SessionInsertRequest.Builder();
        insertRequestBuilder.setSession(session)
                .addAggregateDataPoint(summaryDataPoint)
                .addDataSet(mDistanceDataSet)
                .addDataSet(mLocationDataSet)
                .addDataSet(mSegmentDataSet);

        return insertRequestBuilder.build();
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
}
