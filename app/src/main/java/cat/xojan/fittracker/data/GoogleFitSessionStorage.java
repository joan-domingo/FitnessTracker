package cat.xojan.fittracker.data;

import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.request.DataDeleteRequest;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.SessionReadResult;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import cat.xojan.fittracker.Constant;
import cat.xojan.fittracker.domain.SessionRepository;

public class GoogleFitSessionStorage implements SessionRepository {

    @Override
    public void saveSession(SessionInsertRequest sessionInsertRequest,
                            GoogleApiClient googleApiClient) {
        Status insertStatus = Fitness.SessionsApi.insertSession(googleApiClient,
                sessionInsertRequest).await(1, TimeUnit.MINUTES);

        if (!insertStatus.isSuccess()) {
            Log.i(Constant.TAG, insertStatus.getStatusMessage());
        } else {
            Log.i(Constant.TAG, "Session insert was successful!");
        }
    }

    @Override
    public void deleteSession(DataDeleteRequest dataDeleteRequest,
                              GoogleApiClient googleApiClient) {
        Fitness.HistoryApi.deleteData(googleApiClient, dataDeleteRequest)
                .setResultCallback(status -> {
                    if (status.isSuccess()) {
                        Log.i(Constant.TAG, "Successfully deleted data");
                    } else {
                        Log.i(Constant.TAG, "Failed to delete data");
                    }
                });
    }

    @Override
    public SessionReadResult getSessions(SessionReadRequest sessionReadRequest, GoogleApiClient googleApiClient) {
        return Fitness.SessionsApi.readSession(googleApiClient, sessionReadRequest)
                .await(1, TimeUnit.MINUTES);
    }
}
