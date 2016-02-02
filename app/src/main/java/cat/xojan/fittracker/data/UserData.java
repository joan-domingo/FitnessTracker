package cat.xojan.fittracker.data;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.fitness.data.Session;

import java.util.List;

import cat.xojan.fittracker.FitTrackerApp;

/**
 * Contains user's app session data. eg: google api client.
 */
public class UserData {

    private final Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private List<Session> mFitnessSessions;

    public UserData(Context context) {
        mContext = context;
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        mGoogleApiClient = googleApiClient;
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }

    public void setFitnessSessions(List<Session> sessions) {
        mFitnessSessions = sessions;
    }
}
