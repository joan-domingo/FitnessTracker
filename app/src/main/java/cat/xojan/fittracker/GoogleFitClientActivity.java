package cat.xojan.fittracker;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.schedulers.Schedulers;

public class GoogleFitClientActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        MessageApi.MessageListener {

    private static final String INSERT_REQUEST = "cat.xojan.fittracker.insertrequest";
    private GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_OAUTH = 1;
    /**
     *  Track whether an authorization activity is stacking over the current activity, i.e. when
     *  a known auth error is being resolved, such as showing the account chooser or presenting a
     *  consent dialog. This avoids common duplications as might happen on screen rotations, etc.
     */
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(Constant.AUTH_PENDING);
        }


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.SESSIONS_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(Constant.TAG, "Connected!!!");
    }

    @Override
    public void onConnectionSuspended(int i) {
        // If your connection to the sensor gets lost at some point,
        // you'll be able to determine the reason and react to it here.
        if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
            Log.i(Constant.TAG, "Connection lost.  Cause: Network Lost.");
        } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
            Log.i(Constant.TAG, "Connection lost.  Reason: Service Disconnected");
        }
    }

    private void insertRequest(byte[] buf) {
        // convert back from bytes to object
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(buf));
            SessionInsertRequest insertRequest = (SessionInsertRequest) ois.readObject();
            ois.close();

            Observable.just(insertRequest)
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(request -> {
                        Status insertStatus = Fitness.SessionsApi.insertSession(mGoogleApiClient,
                                insertRequest)
                                .await(1, TimeUnit.MINUTES);

                        if (!insertStatus.isSuccess()) {
                            Log.i(Constant.TAG, "There was a problem inserting the session: " +
                                    insertStatus.getStatusMessage());
                        } else {
                            Log.i(Constant.TAG, "Session insert was successful!");
                        }

                    });

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(Constant.TAG, "Connection failed. Cause: " + result.toString());
        if (!result.hasResolution()) {
            // Show the localized error dialog
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
                    this, 0).show();
            return;
        }
        // The failure has a resolution. Resolve it.
        // Called typically when the app is not yet authorized, and an
        // authorization dialog is displayed to the user.
        if (!authInProgress) {
            try {
                Log.i(Constant.TAG, "Attempting to resolve failed connection");
                authInProgress = true;
                result.startResolutionForResult(this,
                        REQUEST_OAUTH);
            } catch (IntentSender.SendIntentException e) {
                Log.e(Constant.TAG,
                        "Exception while starting resolution activity", e);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Connect to the Fitness API
        Log.i(Constant.TAG, "Connecting...");
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constant.REQUEST_OAUTH) {
            authInProgress = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            } else if (!mGoogleApiClient.isConnected()) {
                finish();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(Constant.AUTH_PENDING, authInProgress);
    }

    @Override
    protected void onResume() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

    }
}
