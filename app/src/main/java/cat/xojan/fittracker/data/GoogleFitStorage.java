package cat.xojan.fittracker.data;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.SessionReadResult;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import cat.xojan.fittracker.domain.GoogleFitRepository;

/**
 * Created by Joan on 23/01/2016.
 */
public class GoogleFitStorage implements GoogleFitRepository {

    @Override
    public SessionReadResult readHistory(Date lastUpdate, GoogleApiClient googleApiClient) {
        //create read request
        long startTime = lastUpdate.getTime();
        long endTime = new Date().getTime();
        SessionReadRequest readRequest = new SessionReadRequest.Builder()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .enableServerQueries()
                .readSessionsFromAllApps()
                .read(DataType.AGGREGATE_ACTIVITY_SUMMARY)
                .read(DataType.TYPE_DISTANCE_DELTA)
                .read(DataType.TYPE_LOCATION_SAMPLE)
                .read(DataType.TYPE_ACTIVITY_SEGMENT)
                .build();

        return Fitness.SessionsApi.readSession(googleApiClient, readRequest).await();
    }
}
