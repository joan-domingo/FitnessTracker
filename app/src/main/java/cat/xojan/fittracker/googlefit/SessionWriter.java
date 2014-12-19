package cat.xojan.fittracker.googlefit;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.request.SessionInsertRequest;

import java.util.concurrent.TimeUnit;

import cat.xojan.fittracker.Constant;

/**
 * Created by Joan on 15/12/2014.
 */
public class SessionWriter extends AsyncTask<SessionInsertRequest, Void, Void> {

    private final GoogleApiClient mClient;

    public SessionWriter(GoogleApiClient client) {
        mClient = client;
    }

    @Override
    protected Void doInBackground(SessionInsertRequest... params) {
        if (params == null || params[0] == null) {
            return null;
        }
        SessionInsertRequest insertRequest = params[0];
        // Then, invoke the Sessions API to insert the session and await the result,
        // which is possible here because of the AsyncTask. Always include a timeout when
        // calling await() to avoid hanging that can occur from the service being shutdown
        // because of low memory or other conditions.
        Log.i(Constant.TAG, "Inserting the session in the History API");
        com.google.android.gms.common.api.Status insertStatus =
                Fitness.SessionsApi.insertSession(mClient, insertRequest)
                        .await(1, TimeUnit.MINUTES);

        // Before querying the session, check to see if the insertion succeeded.
        if (!insertStatus.isSuccess()) {
            Log.i(Constant.TAG, "There was a problem inserting the session: " +
                    insertStatus.getStatusMessage());
            return null;
        }

        // At this point, the session has been inserted and can be read.
        Log.i(Constant.TAG, "Session insert was successful!");
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        onFinishSessionWriting();
    }

    public void onFinishSessionWriting() {}
}
