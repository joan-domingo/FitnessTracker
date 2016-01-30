package cat.xojan.fittracker.data;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;

import cat.xojan.fittracker.FitTrackerApp;

/**
 * Contains user's app session data. eg: google api client.
 */
public class UserData {

    private final Context mContext;
    GoogleApiClient mGoogleApiClient;

    public UserData(Context context) {
        mContext = context;
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        mGoogleApiClient = googleApiClient;
    }

    public GoogleApiClient getGoogleApiClient() {
        return mGoogleApiClient;
    }
}
