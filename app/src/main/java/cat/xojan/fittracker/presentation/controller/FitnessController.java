package cat.xojan.fittracker.presentation.controller;

import android.content.Context;
import android.location.Location;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import cat.xojan.fittracker.BuildConfig;

public class FitnessController {

    private static final String PACKAGE_SPECIFIC_PART = BuildConfig.APPLICATION_ID;
    private final TimeController timeController;
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
    private String mFitnessActivity;

    public FitnessController(Context mContext, TimeController timeController) {
        this.mContext = mContext;
        this.timeController = timeController;
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
        /*mSpeedDataSource = new DataSource.Builder()
                .setAppPackageName(mContext)
                .setDataType(Element.DataType.TYPE_SPEED)
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
        mSegmentDataSet = DataSet.create(mSegmentDataSource);*/
    }

    public void storeLocation(Location location) {
        long time = Calendar.getInstance().getTimeInMillis();
        //distance
        DataPoint locationDataPoint = DataPoint.create(mLocationDataSource);
        /*locationDataPoint.setTimeInterval(time, time, TimeUnit.MILLISECONDS);
        locationDataPoint.getValue(Field.FIELD_LATITUDE).setFloat((float) location.getLatitude());
        locationDataPoint.getValue(Field.FIELD_LONGITUDE).setFloat((float) location.getLongitude());
        locationDataPoint.getValue(Field.FIELD_ACCURACY).setFloat(location.getAccuracy());
        locationDataPoint.getValue(Field.FIELD_ALTITUDE).setFloat((float) location.getAltitude());
        mLocationDataSet.add(locationDataPoint);*/
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
        //speedDataPoint.getValue(Field.FIELD_SPEED).setFloat((float) speed);
        mSpeedDataSet.add(speedDataPoint);
    }

    public void setFitnessActivity(String fitnessActivity) {
        mFitnessActivity = fitnessActivity;
    }
}
