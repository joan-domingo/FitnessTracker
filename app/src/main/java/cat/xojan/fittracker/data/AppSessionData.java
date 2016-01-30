package cat.xojan.fittracker.data;

import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Contains user's app session data. eg: google api client.
 */
public class AppSessionData {

    GoogleApiClient mGoogleApiClient;

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        mGoogleApiClient = googleApiClient;
    }
}
