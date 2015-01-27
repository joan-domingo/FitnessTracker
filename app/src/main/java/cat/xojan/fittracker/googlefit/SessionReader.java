package cat.xojan.fittracker.googlefit;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.SessionReadResult;

import java.util.concurrent.TimeUnit;

import cat.xojan.fittracker.Constant;

public class SessionReader extends AsyncTask<SessionReadRequest, Void, SessionReadResult> {

    private GoogleApiClient mClient;

    public SessionReader(GoogleApiClient mClient) {
        this.mClient = mClient;
    }

    @Override
    protected SessionReadResult doInBackground(SessionReadRequest... params) {
        // Invoke the Sessions API to fetch the session with the query and wait for the result
        // of the read request.
        if (params[0] == null || mClient == null) {
            return null;
        }

        SessionReadResult sessionReadResult =
                Fitness.SessionsApi.readSession(mClient, params[0])
                        .await(1, TimeUnit.MINUTES);

        // Get a list of the sessions that match the criteria to check the result.
        Log.i(Constant.TAG, "Session read was successful. Number of returned sessions is: "
                + sessionReadResult.getSessions().size());

        return sessionReadResult;
    }

    @Override
    protected void onPostExecute(SessionReadResult sessionReadResult) {
        getSessionList(sessionReadResult);
    }

    public void getSessionList(SessionReadResult sessionReadResult) {}
}

