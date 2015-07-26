package cat.xojan.fittracker.service;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import cat.xojan.fittracker.BuildConfig;
import cat.xojan.fittracker.ui.activity.StartUpActivity;

public class WearableListener extends WearableListenerService {

    private static final String TAG = WearableListener.class.getSimpleName();
    private static final String LAUNCH_HANDHELD_APP = "/launch_handheld_app";

    private static final String SESSION_START_TIME = "sessionStartTime";
    private static final String SESSION_END_TIME = "sessionEndTime";
    private static final String START_TIME = "startTime";
    private static final String END_TIME = "endTime";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.v(TAG, "onMessageReceived: " + messageEvent);

        if (LAUNCH_HANDHELD_APP.equals(messageEvent.getPath())) {
            Intent i = new Intent();
            i.setClass(this, StartUpActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d(TAG, "onDataChanged: " + dataEvents);

        // Loop through the events
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED
                    && event.getDataItem().getUri().getPath().equals("/session")) {
                Log.d(TAG, "DataItem changed: " + event.getDataItem().getUri());
                extractAndSaveDataItem(event.getDataItem());
            }
        }
    }

    private void extractAndSaveDataItem(DataItem dataItem) {
        GoogleApiClient fitnessClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.SESSIONS_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .build();

        ConnectionResult connectionResult =
                fitnessClient.blockingConnect(30, TimeUnit.SECONDS);

        //extract data
        DataMap dataMap = DataMapItem.fromDataItem(dataItem).getDataMap();

        for (int i = 0; i < dataMap.size(); i++) {
            saveNewWorkout(dataMap.get(String.valueOf(i)), connectionResult, fitnessClient);
        }
    }

    private void saveNewWorkout(DataMap dataMap, ConnectionResult connectionResult,
                                GoogleApiClient fitnessClient) {

        DataMap summary = dataMap.getDataMap("summary");
        DataMap distance = dataMap.getDataMap("distance");
        DataMap sessionDataMap = dataMap.getDataMap("session");
        ArrayList<DataMap> locations = dataMap.getDataMapArrayList("locations");
        ArrayList<DataMap> segments = dataMap.getDataMapArrayList("segments");

        if (!connectionResult.isSuccess()) {
            Log.e(TAG, "Failed to connect to GoogleApiClient.");
            return;
        }

        // Create a session with metadata about the activity.
        Session session = buildSession(sessionDataMap);

        //summary activity (aggregate)
        DataPoint summaryDataPoint = buildActivitySummary(summary);

        //distance
        DataSet distanceDataSet = buildDistanceDataSet(distance);

        //location
        DataSet locationDataSet = buildLocationDataSet(locations);

        //segment
        DataSet segmentDataSet = buildSegmentDataSet(segments);

        // Build a session insert request
        SessionInsertRequest.Builder insertRequestBuilder = new SessionInsertRequest.Builder();
        insertRequestBuilder.setSession(session)
                .addAggregateDataPoint(summaryDataPoint)
                .addDataSet(distanceDataSet)
                .addDataSet(locationDataSet)
                .addDataSet(segmentDataSet);
        ;

        Status insertStatus = Fitness.SessionsApi.insertSession(fitnessClient,
                insertRequestBuilder.build()).await(1, TimeUnit.MINUTES);

        if (!insertStatus.isSuccess()) {
            Log.i(TAG, insertStatus.getStatusMessage());
        } else {
            Log.i(TAG, "Session insert was successful!");
        }
    }

    private DataSet buildSegmentDataSet(ArrayList<DataMap> segments) {
        DataSource segmentDataSource = new DataSource.Builder()
                .setAppPackageName(BuildConfig.APPLICATION_ID)
                .setDataType(DataType.TYPE_ACTIVITY_SEGMENT)
                .setType(DataSource.TYPE_RAW)
                .build();

        DataSet segmentDataSet = DataSet.create(segmentDataSource);

        for (DataMap segment : segments) {
            DataPoint segmentDataPoint = DataPoint.create(segmentDataSource);
            segmentDataPoint.setTimeInterval(segment.getLong(START_TIME), segment.getLong(END_TIME),
                    TimeUnit.MILLISECONDS);
            segmentDataSet.add(segmentDataPoint);
        }

        return segmentDataSet;
    }

    private DataSet buildLocationDataSet(ArrayList<DataMap> locations) {
        DataSource locationDataSource = new DataSource.Builder()
                .setAppPackageName(BuildConfig.APPLICATION_ID)
                .setDataType(DataType.TYPE_LOCATION_SAMPLE)
                .setType(DataSource.TYPE_RAW)
                .build();

        DataSet locationDataSet  = DataSet.create(locationDataSource);

        for (DataMap location : locations) {
            DataPoint locationDataPoint = DataPoint.create(locationDataSource);
            locationDataPoint.setTimeInterval(location.getLong(START_TIME),
                    location.getLong(END_TIME), TimeUnit.MILLISECONDS);
            locationDataPoint.getValue(Field.FIELD_LATITUDE).setFloat((location
                    .getFloat(Field.FIELD_LATITUDE.toString())));
            locationDataPoint.getValue(Field.FIELD_LONGITUDE).setFloat(location
                    .getFloat(Field.FIELD_LONGITUDE.toString()));
            locationDataPoint.getValue(Field.FIELD_ACCURACY).setFloat(location
                    .getFloat(Field.FIELD_ACCURACY.toString()));
            locationDataPoint.getValue(Field.FIELD_ALTITUDE).setFloat(location
                    .getFloat(Field.FIELD_ALTITUDE.toString()));

            locationDataSet.add(locationDataPoint);
        }

        return locationDataSet;
    }

    private DataSet buildDistanceDataSet(DataMap distance) {
        DataSource distanceDataSource = new DataSource.Builder()
                .setAppPackageName(BuildConfig.APPLICATION_ID)
                .setDataType(DataType.TYPE_DISTANCE_DELTA)
                .setType(DataSource.TYPE_RAW)
                .build();

        DataSet distanceDataSet = DataSet.create(distanceDataSource);

        DataPoint distanceDataPoint = DataPoint.create(distanceDataSource);
        distanceDataPoint.setTimeInterval(distance.getLong(SESSION_START_TIME),
                distance.getLong(SESSION_END_TIME), TimeUnit.MILLISECONDS);
        distanceDataPoint.getValue(Field.FIELD_DISTANCE).setFloat(distance
                .getFloat(Field.FIELD_DISTANCE.toString()));

        distanceDataSet.add(distanceDataPoint);

        return distanceDataSet;
    }

    private DataPoint buildActivitySummary(DataMap summary) {
        DataPoint summaryDataPoint = DataPoint.create(new DataSource.Builder()
                .setAppPackageName(BuildConfig.APPLICATION_ID)
                .setDataType(DataType.AGGREGATE_ACTIVITY_SUMMARY)
                .setType(DataSource.TYPE_RAW)
                .build());

        summaryDataPoint.setTimeInterval(summary.getLong(SESSION_START_TIME),
                summary.getLong(SESSION_END_TIME), TimeUnit.MILLISECONDS);
        summaryDataPoint.getValue(Field.FIELD_NUM_SEGMENTS).setInt(summary
                .getInt(Field.FIELD_NUM_SEGMENTS.toString()));
        summaryDataPoint.getValue(Field.FIELD_DURATION).setInt(summary
                .getInt(Field.FIELD_DURATION.toString()));
        summaryDataPoint.getValue(Field.FIELD_ACTIVITY).setActivity(summary
                .getString(Field.FIELD_ACTIVITY.toString()));

        return summaryDataPoint;
    }

    private Session buildSession(DataMap sessionDataMap) {
        return new Session.Builder()
                .setName(sessionDataMap.getString("name"))
                .setDescription(sessionDataMap.getString("description"))
                .setIdentifier(sessionDataMap.getString("identifier"))
                .setActivity(sessionDataMap.getString("activity"))
                .setStartTime(sessionDataMap.getLong(SESSION_START_TIME), TimeUnit.MILLISECONDS)
                .setEndTime(sessionDataMap.getLong(SESSION_END_TIME), TimeUnit.MILLISECONDS)
                .build();
    }
}
