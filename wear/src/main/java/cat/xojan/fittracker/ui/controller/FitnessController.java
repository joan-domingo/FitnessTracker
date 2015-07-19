package cat.xojan.fittracker.ui.controller;

import android.location.Location;

import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import cat.xojan.fittracker.BuildConfig;

public class FitnessController {

    private static final String SESSION_START_TIME = "sessionStartTime";
    private static final String SESSION_END_TIME = "sessionEndTime";
    private static final String START_TIME = "startTime";
    private static final String END_TIME = "endTime";

    private static FitnessController instance;
    private final TimeController mTimeController;
    private String mFitnessActivity;
    private int mNumSegments;

    private List<DataMap> mSegmentDataMapList;
    private List<DataMap> mLocationDataMapList;

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

        mNumSegments++;

        //segment data map
        DataMap singleSegmentDataMap = new DataMap();
        singleSegmentDataMap.putLong(START_TIME, startTimeSegment);
        singleSegmentDataMap.putLong(END_TIME, endTimeSegment);
        singleSegmentDataMap.putString(Field.FIELD_ACTIVITY.toString(), mFitnessActivity);
        mSegmentDataMapList.add(singleSegmentDataMap);
    }

    public void setFitnessActivity(String activityType) {
        mFitnessActivity = activityType;
    }

    public void init() {
        mNumSegments = 0;
        mSegmentDataMapList = new ArrayList<>();
        mLocationDataMapList = new ArrayList<>();
    }

    public PutDataRequest getSessionData() {
        //summary activity (aggregate)
        DataMap summaryDataMap = new DataMap();
        summaryDataMap.putLong(SESSION_START_TIME, mTimeController.getSessionStartTime());
        summaryDataMap.putLong(SESSION_END_TIME, mTimeController.getSessionEndTime());
        summaryDataMap.putInt(Field.FIELD_NUM_SEGMENTS.toString(), mNumSegments);
        summaryDataMap.putInt(Field.FIELD_DURATION.toString(),
                (int) mTimeController.getSessionWorkoutTime());
        summaryDataMap.putString(Field.FIELD_ACTIVITY.toString(), mFitnessActivity);

        //distance
        DataMap distanceDataMap = new DataMap();
        distanceDataMap.putLong(SESSION_START_TIME, mTimeController.getSessionStartTime());
        distanceDataMap.putLong(SESSION_END_TIME, mTimeController.getSessionEndTime());
        distanceDataMap.putFloat(Field.FIELD_DISTANCE.toString(),
                DistanceController.getInstance().getTotalDistance());

        // Create a session with metadata about the activity.
        DataMap sessionDataMap = new DataMap();
        sessionDataMap.putString("name", "name");
        sessionDataMap.putString("description", "description");
        sessionDataMap.putString("identifier",
                BuildConfig.APPLICATION_ID + ":" + mTimeController.getSessionStartTime());
        sessionDataMap.putString("activity", mFitnessActivity);
        sessionDataMap.putLong(SESSION_START_TIME, mTimeController.getSessionStartTime());
        sessionDataMap.putLong(SESSION_END_TIME, mTimeController.getSessionEndTime());

        // Set the path of the data map
        String path = "/session";
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(path);

        putDataMapRequest.getDataMap().putDataMap("summary", summaryDataMap);
        putDataMapRequest.getDataMap().putDataMap("distance", distanceDataMap);
        putDataMapRequest.getDataMap().putDataMap("session", sessionDataMap);
        putDataMapRequest.getDataMap().putDataMapArrayList("locations",
                (ArrayList<DataMap>) mLocationDataMapList);
        putDataMapRequest.getDataMap().putDataMapArrayList("segments",
                (ArrayList<DataMap>) mSegmentDataMapList);

        return putDataMapRequest.asPutDataRequest();
    }

    public void storeLocation(Location location) {
        long time = Calendar.getInstance().getTimeInMillis();
        //location data map
        DataMap singleLocationDataMap = new DataMap();
        singleLocationDataMap.putLong(START_TIME, time);
        singleLocationDataMap.putLong(END_TIME, time);
        singleLocationDataMap.putFloat(Field.FIELD_LATITUDE.toString(),
                (float) location.getLatitude());
        singleLocationDataMap.putFloat(Field.FIELD_LONGITUDE.toString(),
                (float) location.getLongitude());
        singleLocationDataMap.putFloat(Field.FIELD_ACCURACY.toString(),
                location.getAccuracy());
        singleLocationDataMap.putFloat(Field.FIELD_ALTITUDE.toString(),
                (float) location.getAltitude());
        mLocationDataMapList.add(singleLocationDataMap);
    }
}
